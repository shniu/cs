pipeline {
    agent {
        label 'default'
    }
    stages {
        stage('build') {
            steps {
                echo 'Build...'
                sh 'mvn --version'
            }
        }
    }
}
