daemon=`netstat -tlnp | grep :::21000 | wc -l`
if [ "$daemon" -eq "0" ] ; then
        nohup java -jar /home/bsscco/data-request/data-request-*.jar &
fi