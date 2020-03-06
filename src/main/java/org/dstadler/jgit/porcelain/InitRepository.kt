package org.dstadler.jgit.porcelain

import org.apache.commons.io.FileUtils
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.errors.GitAPIException
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import java.io.File
import java.io.IOException

/*
   Copyright 2013, 2014 Dominik Stadler

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */ /**
 * Simple snippet which shows how to initialize a new repository
 *
 * @author dominik.stadler at gmx.at
 */
object InitRepository {

	@Throws(IOException::class, GitAPIException::class)
	@JvmStatic
	fun main(args: Array<String>) {
		// run the init-call
		var dir = File.createTempFile("gitinit", ".test")
		if (!dir.delete()) {
			throw IOException("Could not delete file $dir")
		}
		Git.init()
				.setDirectory(dir)
				.call().use { git -> println("Created a new repository at " + git.repository.directory) }

		// clean up here to not keep using more and more disk-space for these samples
		FileUtils.deleteDirectory(dir)
		dir = File.createTempFile("repoinit", ".test")
		if (!dir.delete()) {
			throw IOException("Could not delete file $dir")
		}
		FileRepositoryBuilder.create(File(dir.absolutePath, ".git")).use { repository -> println("Created a new repository at " + repository.directory) }

		// clean up here to not keep using more and more disk-space for these samples
		FileUtils.deleteDirectory(dir)
	}
}