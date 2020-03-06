package org.dstadler.jgit.unfinished

import org.apache.commons.io.FileUtils
import org.dstadler.jgit.helper.CookbookHelper.openJGitCookbookRepository
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.GitCommand
import org.eclipse.jgit.api.errors.GitAPIException
import org.eclipse.jgit.api.errors.JGitInternalException
import org.eclipse.jgit.errors.NoWorkTreeException
import org.eclipse.jgit.lib.Repository
import java.io.File
import java.io.IOException
import java.util.*

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
 * Note: This snippet is not done and likely does not show anything useful yet
 *
 * Snippet which shows how to mark files as assumed-unchanged (git update-index --assume-unchanged)
 */
object UpdateIndex {
	@Throws(IOException::class, GitAPIException::class)
	@JvmStatic
	fun main(args: Array<String>) {
		openJGitCookbookRepository().use { repo ->
			Git(repo).use { git ->
				val testFile = "README.md"

				// Modify the file
				FileUtils.write(File(testFile), Date().toString(), "UTF-8")
				println("Modified files: " + getModifiedFiles(git))
				AssumeChangedCommand(repo, testFile, true).call()
				println("Modified files after assume-changed: " + getModifiedFiles(git))
				AssumeChangedCommand(repo, testFile, false).call()
				println("Modified files after no-assume-changed: " + getModifiedFiles(git))
				git.checkout().addPath(testFile).call()
				println("Modified files after reset: " + getModifiedFiles(git))
			}
		}
	}

	@Throws(NoWorkTreeException::class, GitAPIException::class)
	private fun getModifiedFiles(git: Git): Set<String> {
		val status = git.status().call()
		return status.modified
	}

	private class AssumeChangedCommand internal constructor(repository: Repository?, private val fileName: String, private val assumeUnchanged: Boolean) : GitCommand<String?>(repository) {
		@Throws(GitAPIException::class)
		override fun call(): String? {
			try {
				val index = repo.lockDirCache()
				val entry = index.getEntry(fileName)
				if (entry != null) {
					entry.isAssumeValid = assumeUnchanged
					index.write()
					index.commit()
					return entry.pathString
				}
			} catch (e: IOException) {
				throw JGitInternalException(e.message, e)
			}
			return null
		}

	}
}