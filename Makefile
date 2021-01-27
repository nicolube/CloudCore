pluginFile = LightFallCorePlugin/target/LightFallCorePlugin-1.0-SNAPSHOT.jar
deploy:
	scp $(pluginFile) developer@dev.lightfall.de:~/deploy/ && ssh developer@dev.lightfall.de /home/developer/./deploy.sh