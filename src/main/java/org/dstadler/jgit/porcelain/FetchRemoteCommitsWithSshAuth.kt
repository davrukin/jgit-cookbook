package org.dstadler.jgit.porcelain

import com.jcraft.jsch.JSch
import com.jcraft.jsch.JSchException
import com.jcraft.jsch.Session
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.errors.GitAPIException
import org.eclipse.jgit.transport.JschConfigSessionFactory
import org.eclipse.jgit.transport.OpenSshConfig.Host
import org.eclipse.jgit.transport.SshSessionFactory
import org.eclipse.jgit.transport.SshTransport
import org.eclipse.jgit.util.FS

/**
 * Simple snippet which shows how to fetch commits from a remote Git repository
 * via ssh protocol authentication.
 *
 * You need to provide proper values for the URL of the Git repository and
 * the location of the private key file
 *
 *
 *
 * Created by zhengmaoshao on 2019/4/3 上午12:38
 */
object FetchRemoteCommitsWithSshAuth {
	private const val REMOTE_URL = "ssh://<user>@<host>:22/<path-to-remote-repo>/"
	private const val PRIVATE_KEY = "/path/to/private_key"

	@Throws(GitAPIException::class)
	@JvmStatic
	fun main(args: Array<String>) {
		val sshSessionFactory: SshSessionFactory = object : JschConfigSessionFactory() {
			override fun configure(host: Host, session: Session) {}

			@Throws(JSchException::class)
			override fun createDefaultJSch(fs: FS): JSch {
				val defaultJSch = super.createDefaultJSch(fs)
				defaultJSch.addIdentity(PRIVATE_KEY)
				return defaultJSch
			}
		}
		val lsRemoteCommand = Git.lsRemoteRepository()
		lsRemoteCommand.setRemote(REMOTE_URL)
		lsRemoteCommand.setTransportConfigCallback { transport ->
			val sshTransport = transport as SshTransport
			sshTransport.sshSessionFactory = sshSessionFactory
		}
		val map = lsRemoteCommand.setHeads(true)
				.setTags(true)
				.callAsMap()
		for ((key, value) in map) {
			println("Key: " + key /*eg.refs/heads/develop*/ + ", Ref: " + value.objectId.name /*eg.e16c937848d5c1ad50ef163003c7b076103f7e37*/)
		}
	}
}