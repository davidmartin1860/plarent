package com.plarent.backend.user

class UsernameTakenException : RuntimeException("Username is already taken")

class InvalidCredentialsException : RuntimeException("Invalid username or password")

class TooManyLoginAttemptsException : RuntimeException("Too many failed login attempts, try again later")
