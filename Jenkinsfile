pipeline {
    agent any
    
    
    stages {
        stage('Build'){
            steps {
                sh 'mvn clean package'
            }
        }
        
        stage('Test'){
            steps {
               sh 'mvn -Dtest=ReceiptTest test'
            }
        }
    }
}
