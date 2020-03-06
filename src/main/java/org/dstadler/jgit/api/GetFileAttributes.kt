package org.dstadler.jgit.api

import org.dstadler.jgit.helper.CookbookHelper.openJGitCookbookRepository
import org.eclipse.jgit.lib.Constants
import org.eclipse.jgit.lib.FileMode
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.revwalk.RevTree
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
 * Snippet which shows how to use RevWalk and TreeWalk to read the file
 * attributes like execution-bit and type of file/directory/...
 *
 * @author dominik.stadler at gmx.at
 */
object GetFileAttributes {

	@Throws(IOException::class)
	@JvmStatic
	fun main(args: Array<String>) {
		openJGitCookbookRepository().use { repository ->
			// find the Tree for current HEAD
			val tree = getTree(repository)
			printFile(repository, tree)
			printDirectory(repository, tree)
		}
	}

	@Throws(IOException::class)
	private fun getTree(repository: Repository): RevTree {
		val lastCommitId = repository.resolve(Constants.HEAD)
		RevWalk(repository).use { revWalk ->
			val commit = revWalk.parseCommit(lastCommitId)
			println("Time of commit (seconds since epoch): " + commit.commitTime)

			// and using commit's tree find the path
			val tree = commit.tree
			println("Having tree: $tree")
			return tree
		}
	}

	@Throws(IOException::class)
	private fun printFile(repository: Repository, tree: RevTree) {
		// now try to find a specific file
		TreeWalk(repository).use { treeWalk ->
			treeWalk.addTree(tree)
			treeWalk.isRecursive = false
			treeWalk.filter = PathFilter.create("README.md")
			check(treeWalk.next()) { "Did not find expected file 'README.md'" }

			// FileMode specifies the type of file, FileMode.REGULAR_FILE for normal file, FileMode.EXECUTABLE_FILE for executable bit
			// set
			val fileMode = treeWalk.getFileMode(0)
			val loader = repository.open(treeWalk.getObjectId(0))
			println("README.md: " + getFileMode(fileMode) + ", type: " + fileMode.objectType + ", mode: " + fileMode +
					" size: " + loader.size)
		}
	}

	@Throws(IOException::class)
	private fun printDirectory(repository: Repository, tree: RevTree) {
		// look at directory, this has FileMode.TREE
		TreeWalk(repository).use { treeWalk ->
			treeWalk.addTree(tree)
			treeWalk.isRecursive = false
			treeWalk.filter = PathFilter.create("src")
			check(treeWalk.next()) { "Did not find expected folder 'src'" }

			// FileMode now indicates that this is a directory, i.e. FileMode.TREE.equals(fileMode) holds true
			val fileMode = treeWalk.getFileMode(0)
			println("src: " + getFileMode(fileMode) + ", type: " + fileMode.objectType + ", mode: " + fileMode)
		}
	}

	private fun getFileMode(fileMode: FileMode): String {
		return if (fileMode == FileMode.EXECUTABLE_FILE) {
			"Executable File"
		} else if (fileMode == FileMode.REGULAR_FILE) {
			"Normal File"
		} else if (fileMode == FileMode.TREE) {
			"Directory"
		} else if (fileMode == FileMode.SYMLINK) {
			"Symlink"
		} else {
			// there are a few others, see FileMode javadoc for details
			throw IllegalArgumentException("Unknown type of file encountered: $fileMode")
		}
	}
}