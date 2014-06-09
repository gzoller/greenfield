package io.recurve

trait Box {
	val server : Server

	val config = server.phoneHome
}