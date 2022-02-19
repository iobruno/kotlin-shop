.PHONY: setup test

setup:
	@./gradlew clean build

test: setup
	@./gradlew test
