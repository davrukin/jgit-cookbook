package org.dstadler.jgit.porcelain

import org.apache.commons.io.FileUtils
import org.dstadler.jgit.helper.CookbookHelper.openJGitCookbookRepository
import org.eclipse.jgit.api.ArchiveCommand
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.errors.GitAPIException
import org.eclipse.jgit.lib.FileMode
import org.eclipse.jgit.lib.ObjectId
import org.eclipse.jgit.lib.ObjectLoader
import org.eclipse.jgit.revwalk.RevCommit
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

/*
 * Copyright 2013, 2014 Dominik Stadler
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */ /**
 * Simple snippet which shows how to package the contents of a branch into an archive file
 * using a custom compression format.
 *
 * @author dominik.stadler at gmx.at
 */
object CreateCustomFormatArchive {

    @Throws(IOException::class, GitAPIException::class)
	@JvmStatic
	fun main(args: Array<String>) {
		val file = File.createTempFile("test", ".mzip")
		openJGitCookbookRepository().use { repository ->
			// make the archive format known
			ArchiveCommand.registerFormat("myzip", ZipArchiveFormat())
			try {
				// this is the file that we write the archive to
				FileOutputStream(file).use { out ->
					Git(repository).use { git ->
						git.archive()
								.setTree(repository.resolve("master"))
								.setFormat("myzip")
								.setOutputStream(out)
								.call()
					}
				}
			} finally {
				ArchiveCommand.unregisterFormat("myzip")
			}
			println("Wrote " + file.length() + " bytes to " + file)
		}

		// clean up here to not keep using more and more disk-space for these samples
		FileUtils.forceDelete(file)
	}

	/**
	 * A simple custom format for Zip-files via ZipOutputStream,
	 * JGit only has one via commons-compress
	 */
	private class ZipArchiveFormat : ArchiveCommand.Format<ZipOutputStream> {
		override fun createArchiveOutputStream(s: OutputStream): ZipOutputStream {
			return ZipOutputStream(s)
		}

		@Throws(IOException::class)
		override fun putEntry(out: ZipOutputStream, tree: ObjectId, path: String, mode: FileMode, loader: ObjectLoader) {
			// loader is null for directories...
			if (loader != null) {
				val entry = ZipEntry(path)
				if (tree is RevCommit) {
					val t = tree.commitTime * 1000L
					entry.time = t
				}
				out.putNextEntry(entry)
				out.write(loader.bytes)
				out.closeEntry()
			}
		}

		override fun suffixes(): Iterable<String> {
			return setOf(".mzip")
		}

		override fun createArchiveOutputStream(s: OutputStream, o: Map<String, Any>): ZipOutputStream {
			return ZipOutputStream(s)
		}
	}
}