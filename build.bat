::cd util
::call gradle uploadArchives

::cd ../
::cd http
::call gradle uploadArchives

::cd ../
::cd zbar
::call gradle uploadArchives

::cd ../
::cd third
::call gradle uploadArchives

::cd ../
::cd common
::call gradle uploadArchives

::cd ../
::cd video
::call gradle uploadArchives

::cd ../
cd image
call gradle uploadArchives

cd ../
cd file
call gradle uploadArchives

cd ../
cd pdf
call gradle uploadArchives

cd ../
cd time
call gradle uploadArchives

pause
exit