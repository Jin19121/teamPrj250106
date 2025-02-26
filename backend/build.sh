rm -rf src/main/resources/static

cd ../frontend
npm run build

mv dist ../backend/src/main/resources/static

cd ../backend

./gradlew bootJar

scp -i src/main/resources/secret/key1121.pem build/libs/backend-0.0.1-SNAPSHOT.jar ubuntu@52.79.239.190:./prj.jar
ssh -i src/main/resources/secret/key1121.pem ubuntu@52.79.239.190 './run.sh'