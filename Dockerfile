FROM java:openjdk-8-jdk

ENV MAVEN_VERSION 3.3.3

RUN curl -fsSL http://archive.apache.org/dist/maven/maven-3/$MAVEN_VERSION/binaries/apache-maven-$MAVEN_VERSION-bin.tar.gz | tar xzf - -C /usr/share \
  && mv /usr/share/apache-maven-$MAVEN_VERSION /usr/share/maven \
  && ln -s /usr/share/maven/bin/mvn /usr/bin/mvn

ENV MAVEN_HOME /usr/share/maven

WORKDIR /source

ADD settings.xml /root/.m2/

ADD pom.xml     /source/pom.xml
RUN mvn install -DskipTests

ADD src/         /source/src/

RUN mvn clean package

RUN chmod 777       /source/target/SwarmDocker-swarm.jar

RUN java -Djboss.bind.address=0.0.0.0 -jar /source/target/SwarmDocker-swarm.jar
