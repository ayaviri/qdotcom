.PHONY: lint build test

lint:
	cd Q/source_files && mvn spotless:apply

test:
	cd Q/source_files && mvn test

build:
	cd Q/source_files && mvn -q clean package shade:shade
