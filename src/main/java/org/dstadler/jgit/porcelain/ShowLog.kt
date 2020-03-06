package org.dstadler.jgit.porcelain

import org.dstadler.jgit.helper.CookbookHelper.openJGitCookbookRepository
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.errors.GitAPIException
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
 * Simple snippet which shows how to get the commit-ids for a file to provide log information.
 *
 * @author dominik.stadler at gmx.at
 */
object ShowLog {

	@Throws(IOException::class, GitAPIException::class)
	@JvmStatic
	fun main(args: Array<String>) {
		openJGitCookbookRepository().use { repository ->
			Git(repository).use { git ->
				var logs = git.log()
						.call()
				var count = 0
				for (rev in logs) {
					//System.out.println("Commit: " + rev /* + ", name: " + rev.getName() + ", id: " + rev.getId().getName() */);
					count++
				}
				println("Had $count commits overall on current branch")
				logs = git.log()
						.add(repository.resolve("remotes/origin/testbranch"))
						.call()
				count = 0
				for (rev in logs) {
					println("Commit: $rev" /* + ", name: " + rev.getName() + ", id: " + rev.getId().getName() */)
					count++
				}
				println("Had $count commits overall on test-branch")
				logs = git.log()
						.not(repository.resolve("master"))
						.add(repository.resolve("remotes/origin/testbranch"))
						.call()
				count = 0
				for (rev in logs) {
					println("Commit: $rev" /* + ", name: " + rev.getName() + ", id: " + rev.getId().getName() */)
					count++
				}
				println("Had $count commits only on test-branch")
				logs = git.log()
						.all()
						.call()
				count = 0
				for (rev in logs) {
					//System.out.println("Commit: " + rev /* + ", name: " + rev.getName() + ", id: " + rev.getId().getName() */);
					count++
				}
				println("Had $count commits overall in repository")
				logs = git.log() // for all log.all()
						.addPath("README.md")
						.call()
				count = 0
				for (rev in logs) {
					//System.out.println("Commit: " + rev /* + ", name: " + rev.getName() + ", id: " + rev.getId().getName() */);
					count++
				}
				println("Had $count commits on README.md")
				logs = git.log() // for all log.all()
						.addPath("pom.xml")
						.call()
				count = 0
				for (rev in logs) {
					//System.out.println("Commit: " + rev /* + ", name: " + rev.getName() + ", id: " + rev.getId().getName() */);
					count++
				}
				println("Had $count commits on pom.xml")
			}
		}
	}
}