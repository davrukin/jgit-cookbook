package org.dstadler.jgit.porcelain

import org.apache.commons.io.FileUtils
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.errors.GitAPIException
import org.eclipse.jgit.lib.ProgressMonitor
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
 * Simple snippet which shows how to clone a repository from GitHub and
 * then checkout a PR
 *
 * @author dominik.stadler at gmx.at
 */
object CheckoutGitHubPullRequest {

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
				.setProgressMonitor(SimpleProgressMonitor())
				.call().use { result ->
					// Note: the call() returns an opened repository already which needs to be closed to avoid file handle leaks!
					println("Having repository: " + result.repository.directory)
					val fetchResult = result.fetch()
							.setRemote(REMOTE_URL)
							.setRefSpecs("+refs/pull/6/head:pr_6") //.setRefSpecs(new RefSpec("+refs/heads/*:refs/heads/*"))
							.call()
					println("Result when fetching the PR: " + fetchResult.messages)
					val checkoutRef = result.checkout()
							.setName("pr_6")
							.call()
					println("Checked out PR, now printing log, it should include two commits from the PR on top")
					val logs = result.log()
							.call()
					for (rev in logs) {
						println("Commit: $rev" /* + ", name: " + rev.getName() + ", id: " + rev.getId().getName() */)
					}
				}

		// clean up here to not keep using more and more disk-space for these samples
		FileUtils.deleteDirectory(localPath)
	}

	private class SimpleProgressMonitor : ProgressMonitor {
		override fun start(totalTasks: Int) {
			println("Starting work on $totalTasks tasks")
		}

		override fun beginTask(title: String, totalWork: Int) {
			println("Start $title: $totalWork")
		}

		override fun update(completed: Int) {
			print("$completed-")
		}

		override fun endTask() {
			println("Done")
		}

		override fun isCancelled(): Boolean {
			return false
		}
	}
}