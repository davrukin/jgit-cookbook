package org.dstadler.jgit.porcelain

import org.apache.commons.io.IOUtils
import org.dstadler.jgit.helper.CookbookHelper.openJGitCookbookRepository
import org.eclipse.jgit.api.BlameCommand
import org.eclipse.jgit.api.errors.GitAPIException
import org.eclipse.jgit.lib.ObjectId
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.revwalk.RevWalk
import org.eclipse.jgit.treewalk.TreeWalk
import org.eclipse.jgit.treewalk.filter.PathFilter
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.FileInputStream
import java.io.IOException

/*
    Copyright 2013, 2014, 2017 Dominik Stadler

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
 * Simple snippet which shows how to get a diff showing who
 * changed which line in a file.
 *
 * It uses HEAD~~ to select the version of README.md two commits ago
 * and reads the blame information for it.
 *
 * Then it prints out the number of lines and the actual number of lines in the
 * latest/local version of the file.
 *
 * @author dominik.stadler at gmx.at
 */
object ShowBlame {

	@Throws(IOException::class, GitAPIException::class)
	@JvmStatic
	fun main(args: Array<String>) {
		// prepare a new test-repository
		openJGitCookbookRepository().use { repository ->
			val blamer = BlameCommand(repository)
			val commitID = repository.resolve("HEAD~~")
			blamer.setStartCommit(commitID)
			blamer.setFilePath("README.md")
			val blame = blamer.call()

			// read the number of lines from the given revision, this excludes changes from the last two commits due to the "~~" above
			val lines = countLinesOfFileInCommit(repository, commitID, "README.md")
			for (i in 0 until lines) {
				val commit = blame.getSourceCommit(i)
				println("Line: $i: $commit")
			}
			var currentLines: Int = 0
			FileInputStream("README.md").use { input -> currentLines = IOUtils.readLines(input, "UTF-8").size }
			println("Displayed commits responsible for $lines lines of README.md, current version has $currentLines lines")
		}
	}

	@Throws(IOException::class)
	private fun countLinesOfFileInCommit(repository: Repository, commitID: ObjectId, name: String): Int {
		RevWalk(repository).use { revWalk ->
			val commit = revWalk.parseCommit(commitID)
			val tree = commit.tree
			println("Having tree: $tree")
			TreeWalk(repository).use { treeWalk ->
				treeWalk.addTree(tree)
				treeWalk.isRecursive = true
				treeWalk.filter = PathFilter.create(name)
				check(treeWalk.next()) { "Did not find expected file 'README.md'" }
				val objectId = treeWalk.getObjectId(0)
				val loader = repository.open(objectId)

				// load the content of the file into a stream
				val stream = ByteArrayOutputStream()
				loader.copyTo(stream)
				revWalk.dispose()
				return IOUtils.readLines(ByteArrayInputStream(stream.toByteArray()), "UTF-8").size
			}
		}
	}
}