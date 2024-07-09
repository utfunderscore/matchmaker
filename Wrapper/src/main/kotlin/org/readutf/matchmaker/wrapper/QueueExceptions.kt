package org.readutf.matchmaker.wrapper

class ServiceUnreachableException : Exception("Matchmaker remote service is unreachable.")

class QueueNotFoundException : Exception("Queue not found.")

class InvalidTeamsException : Exception("Invalid player teams.")
