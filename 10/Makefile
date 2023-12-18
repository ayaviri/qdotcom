build:
	cd ../Q/source_files && \
	sed -i.bu 's/<main.class>.*<\/main.class>/<main.class>chen_ayaviri.common.XServer<\/main.class>/g' pom.xml && mvn -q clean package shade:shade && \
	rm pom.xml && mv pom.xml.bu pom.xml
