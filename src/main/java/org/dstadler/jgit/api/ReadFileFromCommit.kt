package org.dstadler.jgit.api

import org.dstadler.jgit.helper.CookbookHelper.openJGitCookbookRepository
import org.eclipse.jgit.lib.Constants
import org.eclipse.jgit.revwalk.RevWalk
import org.eclipse.jgit.treewalk.TreeWalk
import org.eclipse.jgit.treewalk.filter.PathFilter
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
 * Snippet which shows how to use RevWalk and TreeWalk to read the contents
 * of a specific file from a specific commit.
 *
 * @author dominik.stadler at gmx.at
 */
object ReadFileFromCommit {

	@Throws(IOException::class)
	@JvmStatic
	fun main(args: Array<String>) {
		openJGitCookbookRepository().use { repository ->
			// find the HEAD
			val lastCommitId = repository.resolve(Constants.HEAD)
			RevWalk(repository).use { revWalk ->
				val commit = revWalk.parseCommit(lastCommitId)
				// and using commit's tree find the path
				val tree = commit.tree
				println("Having tree: $tree")
				TreeWalk(repository).use { treeWalk ->
					treeWalk.addTree(tree)
					treeWalk.isRecursive = true
					treeWalk.filter = PathFilter.create("README.md")
					check(treeWalk.next()) { "Did not find expected file 'README.md'" }
					val objectId = treeWalk.getObjectId(0)
					val loader = repository.open(objectId)

					// and then one can the loader to read the file
					loader.copyTo(System.out)
				}
				revWalk.dispose()
			}
		}
	}
}