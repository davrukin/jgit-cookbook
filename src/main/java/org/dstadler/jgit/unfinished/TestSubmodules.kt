package org.dstadler.jgit.unfinished

import org.apache.commons.io.FileUtils
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.errors.GitAPIException
import org.eclipse.jgit.lib.Repository
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
 * Note: This snippet is not done and likely does not show anything useful yet
 *
 * @author dominik.stadler at gmx.at
 */
object TestSubmodules {
	@Throws(IOException::class, GitAPIException::class)
	@JvmStatic
	fun main(args: Array<String>) {
		val mainRepoDir = createRepository()
		openMainRepo(mainRepoDir).use { mainRepo ->
			addSubmodule(mainRepo)
			val builder = FileRepositoryBuilder()
			builder.setGitDir(File("testrepo/.git"))
					.readEnvironment() // scan environment GIT_* variables
					.findGitDir() // scan up the file system tree
					.build().use { subRepo ->
						check(!subRepo.isBare) { "Repository at " + subRepo.directory + " should not be bare" }
					}
		}
		println("All done!")

		// clean up here to not keep using more and more disk-space for these samples
		FileUtils.deleteDirectory(mainRepoDir)
	}

	@Throws(GitAPIException::class)
	private fun addSubmodule(mainRepo: Repository) {
		println("Adding submodule")
		Git(mainRepo).use { git ->
			git.submoduleAdd().setURI("https://github.com/github/testrepo.git").setPath("testrepo").call().use { subRepoInit ->
				check(!subRepoInit.isBare) { "Repository at " + subRepoInit.directory + " should not be bare" }
			}
		}
	}

	@Throws(IOException::class)
	private fun openMainRepo(mainRepoDir: File): Repository {
		val builder = FileRepositoryBuilder()
		val mainRepo = builder.setGitDir(File(mainRepoDir.absolutePath, ".git"))
				.readEnvironment() // scan environment GIT_* variables
				.findGitDir() // scan up the file system tree
				.build()
		check(!mainRepo.isBare) { "Repository at $mainRepoDir should not be bare" }
		return mainRepo
	}

	@Throws(IOException::class, GitAPIException::class)
	private fun createRepository(): File {
		val dir = File.createTempFile("gitinit", ".test")
		if (!dir.delete()) {
			throw IOException("Could not delete temporary file $dir")
		}
		Git.init()
				.setDirectory(dir)
				.call()
		FileRepositoryBuilder.create(File(dir.absolutePath, ".git")).use { repository -> println("Created a new repository at " + repository.directory) }
		return dir
	}
}