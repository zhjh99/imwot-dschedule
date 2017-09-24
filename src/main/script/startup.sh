APP_HOME=$(cd "$(dirname "$0")"; pwd)
APP_RESOURCE=$APP_HOME/etc:$APP_HOME/resource
APP_JARS=$APP_HOME/lib/*


JAVA_OPTS="-Xms256m -Xmx256m"
#JAVA_OPTS="$JAVA_OPTS -Djava.rmi.server.hostname=192.168.2.229"
#JAVA_OPTS="$JAVA_OPTS -Dcom.sun.management.jmxremote.port=5555"
#JAVA_OPTS="$JAVA_OPTS -Dcom.sun.management.jmxremote.authenticate=false"
#JAVA_OPTS="$JAVA_OPTS -Dcom.sun.management.jmxremote.ssl=false"

if [ "$APP_HOME"x != x ]; then
	echo "app_home is......"
	echo $APP_HOME
	echo "app_jars is......"
	echo $APP_JARS
	nohup java $JAVA_OPTS -classpath .:$APP_RESOURCE:$APP_JARS com.allinpay.crawler.StartUp >/dev/null 2>&1 &
	echo $!> $APP_HOME/.DScheduler.pid
else
	echo "app_home is null!"
fi
