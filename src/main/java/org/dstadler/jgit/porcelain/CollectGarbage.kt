package org.dstadler.jgit.porcelain

import org.dstadler.jgit.helper.CookbookHelper.openJGitCookbookRepository
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.errors.GitAPIException
import org.eclipse.jgit.lib.ProgressMonitor
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
 * Simple snippet which shows how to execute the "gc" command to remove unused
 * objects from the .git directory.
 *
 * @author dominik.stadler at gmx.at
 */
object CollectGarbage {
	@Throws(IOException::class, GitAPIException::class)
	@JvmStatic
	fun main(args: Array<String>) {
		openJGitCookbookRepository().use { repository ->
			Git(repository).use { git ->
				val ret = git.gc().setProgressMonitor(PrintlnProgressMonitor()).call()
				for ((key, value) in ret) {
					println("Ret: $key: $value")
				}
			}
		}
	}

	private class PrintlnProgressMonitor : ProgressMonitor {
		override fun start(totalTasks: Int) {
			println("Starting work on $totalTasks tasks")
		}

		override fun beginTask(title: String, totalWork: Int) {
			println("Start $title: $totalWork")
		}

		override fun update(completed: Int) {
			print(completed)
		}

		override fun endTask() {
			println("Done")
		}

		override fun isCancelled(): Boolean {
			return false
		}
	}
}