package org.dstadler.jgit.api

import org.dstadler.jgit.helper.CookbookHelper.openJGitCookbookRepository
import org.eclipse.jgit.lib.FileMode
import org.eclipse.jgit.lib.ObjectId
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.revwalk.RevCommit
import org.eclipse.jgit.revwalk.RevTree
import org.eclipse.jgit.revwalk.RevWalk
import org.eclipse.jgit.treewalk.TreeWalk
import java.io.FileNotFoundException
import java.io.IOException
import java.util.*

/*
   Copyright 2016 Dominik Stadler

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
 * Simple snippet which shows how to get a list of files/directories
 * based on a specific commit or a tag.
 */
object ListFilesOfCommitAndTag {
	@Throws(IOException::class)
	@JvmStatic
	fun main(args: Array<String>) {
		openJGitCookbookRepository().use { repository ->
			var paths = readElementsAt(repository, "6409ee1597a53c6fbee31edf9cde31dc3afbe20f", "src/main/java/org/dstadler/jgit/porcelain")
			println("Had paths for commit: $paths")
			val testbranch = repository.resolve("testbranch")
			paths = readElementsAt(repository, testbranch.name, "src/main/java/org/dstadler/jgit/porcelain")
			println("Had paths for tag: $paths")
		}
	}

	@Throws(IOException::class)
	private fun readElementsAt(repository: Repository, commit: String, path: String): List<String?> {
		val revCommit = buildRevCommit(repository, commit)

		// and using commit's tree find the path
		val tree = revCommit.tree
		//System.out.println("Having tree: " + tree + " for commit " + commit);
		val items: MutableList<String?> = ArrayList()

		// shortcut for root-path
		if (path.isEmpty()) {
			TreeWalk(repository).use { treeWalk ->
				treeWalk.addTree(tree)
				treeWalk.isRecursive = false
				treeWalk.isPostOrderTraversal = false
				while (treeWalk.next()) {
					items.add(treeWalk.pathString)
				}
			}
		} else {
			// now try to find a specific file
			buildTreeWalk(repository, tree, path).use { treeWalk ->
				check(treeWalk.getFileMode(0).bits and FileMode.TYPE_TREE != 0) { "Tried to read the elements of a non-tree for commit '" + commit + "' and path '" + path + "', had filemode " + treeWalk.getFileMode(0).bits }
				TreeWalk(repository).use { dirWalk ->
					dirWalk.addTree(treeWalk.getObjectId(0))
					dirWalk.isRecursive = false
					while (dirWalk.next()) {
						items.add(dirWalk.pathString)
					}
				}
			}
		}
		return items
	}

	@Throws(IOException::class)
	private fun buildRevCommit(repository: Repository, commit: String): RevCommit {
		// a RevWalk allows to walk over commits based on some filtering that is defined
		RevWalk(repository).use { revWalk -> return revWalk.parseCommit(ObjectId.fromString(commit)) }
	}

	@Throws(IOException::class)
	private fun buildTreeWalk(repository: Repository, tree: RevTree, path: String): TreeWalk {
		return TreeWalk.forPath(repository, path, tree)
				?: throw FileNotFoundException("Did not find expected file '" + path + "' in tree '" + tree.name + "'")
	}
}