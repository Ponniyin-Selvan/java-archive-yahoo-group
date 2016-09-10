YAG_LIB=lib
YAG_CP=$YAG_LIB/activation.jar:$YAG_LIB/commons-codec-1.3.jar:$YAG_LIB/commons-collections-3.2.jar
YAG_CP=$YAG_CP:$YAG_LIB/commons-httpclient-3.0.1.jar:$YAG_LIB/commons-io-1.2.jar
YAG_CP=$YAG_CP:$YAG_LIB/commons-lang-2.1.jar:$YAG_LIB/commons-logging-1.1.jar
YAG_CP=$YAG_CP:$YAG_LIB/htmlunit-1.10.jar:$YAG_LIB/jakarta-oro-2.0.8.jar
YAG_CP=$YAG_CP:$YAG_LIB/jaxen-1.1-beta-11.jar:$YAG_LIB/js.jar:$YAG_LIB/log4j.jar:$YAG_LIB/mail.jar
YAG_CP=$YAG_CP:$YAG_LIB/mysql-connector-java-5.0.5-bin.jar:$YAG_LIB/nekohtml-0.9.5.jar
YAG_CP=$YAG_CP:$YAG_LIB/xercesImpl-2.6.2.jar:$YAG_LIB/xmlParserAPIs-2.6.2.jar

YAG_CLASS=net.namonamaha.archive.ygroups.ArchiveYahooGroup

YAG_PROXY=

#REM %1 - // Yahoo Login Id
#REM %2 - // Yahoo Login Password
#REM %3 - // Download from message no
#REM %4 - // Download x no of messages
#REM %5 - // Batch Count
#REM %6 - // Sleep time for each Batch Count in milliseconds 1000 => 1 second

#REM java -cp bin:etc:$YAG_CP $YAG_PROXY $YAG_CLASS %1 %2 %3 %4 %5 %6
java -Xmx8m -cp bin:etc:$YAG_CP $YAG_PROXY $YAG_CLASS ps.archive ps12345 0 -1 1 8000
