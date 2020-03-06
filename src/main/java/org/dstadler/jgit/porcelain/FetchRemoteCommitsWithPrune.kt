package org.dstadler.jgit.porcelain

import org.apache.commons.io.FileUtils
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.ListBranchCommand.ListMode
import org.eclipse.jgit.api.errors.GitAPIException
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
 * Simple snippet which shows how to fetch commits from a remote Git repository
 *
 * @author dominik.stadler at gmx.at
 */
object FetchRemoteCommitsWithPrune {
	private const val REMOTE_URL = "https://github.com/github/testrepo.git"

	@Throws(IOException::class, GitAPIException::class)
	@JvmStatic
	fun main(args: Array<String>) {
		// prepare a new folder for the cloned repository
		val localPath = File.createTempFile("TestGitRepository", "")
		if (!localPath.delete()) {
			throw IOException("Could not delete temporary file $localPath")
		}

		// then clone
		println("Cloning from $REMOTE_URL to $localPath")
		Git.cloneRepository()
				.setURI(REMOTE_URL)
				.setDirectory(localPath)
				.call().use { git ->
					// Note: the call() returns an opened repository already which needs to be closed to avoid file handle leaks!
					println("Having repository: " + git.repository.directory)
					println("Starting fetch")
					val result = git.fetch().setCheckFetchedObjects(true).call()
					println("Messages: " + result.messages)

					// ensure master/HEAD are still there
					println("Listing local branches:")
					var call = git.branchList().call()
					for (ref in call) {
						println("Branch: " + ref + " " + ref.name + " " + ref.objectId.name)
					}
					println("Now including remote branches:")
					call = git.branchList().setListMode(ListMode.ALL).call()
					for (ref in call) {
						println("Branch: " + ref + " " + ref.name + " " + ref.objectId.name)
					}
				}

		// clean up here to not keep using more and more disk-space for these samples
		FileUtils.deleteDirectory(localPath)
	}
}