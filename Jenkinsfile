pipeline {
    agent any

    environment {
        APP_NAME = 'india-weather-live'
        CONTAINER_NAME = 'india-weather-live-container'
    }

    stages {

        stage('Checkout') {
            steps {
                git branch: 'master',
                    url: 'https://github.com/ritupatel1104/Weather-app-INDIA.git'
            }
        }

        stage('Build WAR') {
            steps {
                sh 'chmod +x mvnw'
                sh './mvnw clean package'
            }
        }

        stage('Deploy to Tomcat') {
            steps {
                sh 'rm -rf /opt/tomcat/webapps/ROOT* || true'
                sh 'cp target/*.war /opt/tomcat/webapps/ROOT.war'
                sh '/opt/tomcat/bin/shutdown.sh || true'
                sh 'sleep 5'
                sh '/opt/tomcat/bin/startup.sh'
            }
        }

        stage('Build Docker Image') {
            steps {
                sh 'docker build -t $APP_NAME .'
            }
        }

        stage('Run Docker Container') {
            steps {
                sh 'docker rm -f $CONTAINER_NAME || true'
                sh 'docker run -d --name $CONTAINER_NAME -p 8081:8080 $APP_NAME'
            }
        }

        stage('Verify') {
            steps {
                sh 'docker ps'
                sh 'ls -lh /opt/tomcat/webapps'
            }
        }
    }

    post {
        success {
            echo 'Tomcat + Docker deployment completed successfully!'
        }

        failure {
            echo 'Pipeline failed!'
        }
    }
}
