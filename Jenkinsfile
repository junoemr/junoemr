pipeline {
    agent {
        docker {
            image 'maven:3.8-openjdk-16'
            args '-v $HOME/.m2:/root/.m2'
        }
    }
    stages {
        stage('Test') {
            steps {
                sh 'mvn -Doscar.dbinit.skip=false clean verify'
            }
        }
    }
}