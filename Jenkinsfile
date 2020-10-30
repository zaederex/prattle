pipeline {
 environment {
   
   jobBaseName = "${env.JOB_NAME}".split("/").getAt(1..1).toString().replaceAll('\\[','').replaceAll('\\]','')
 }
 options {
  timeout(time: 10, unit: 'MINUTES') 
  } //option

  agent none
  stages {

   stage("Build and Test") {
     when {
       not {
         branch 'master'
       }
     }
     agent {
      docker {
        image 'cschockaert/docker-npm-maven:latest'
        args '-v m2_repos:/root/.m2'
                } //docker
            } // agent

            stages {

             stage('Build') {
               steps {
                echo "${jobBaseName}"
                 //sh 'mvn -f Development/pom.xml install'
                 echo "Building ChatServer"
                 sh 'mvn -f Development/pom.xml compile'
               }
   } // build
   stage('SonarQube') {
     when {
       not {
         branch 'master'
       }
     }
     steps {
      withSonarQubeEnv('SonarQube') {
        sh 'mvn -f Development/pom.xml clean install'
        sh 'mvn -f Development/pom.xml sonar:sonar -Dsonar.projectKey=${jobBaseName} -Dsonar.projectName=${jobBaseName} -Dsonar.exclusions=**/*.js'
      }

      sh 'sleep 30'
      timeout(time: 10, unit: 'SECONDS') {
       retry(5) {
        script {
          def qg = waitForQualityGate()
          if (qg.status != 'OK') {
            error "Pipeline aborted due to quality gate failure: ${qg.status}"
          }
        }
      }
    }
  } //steps
} //SONAR
}
} //stage 
stage('Master Branch Tasks') {
  when {
   branch 'master'
 }
 agent any
 steps {
   echo "Building ChatServer"
   sh 'mvn -f Development/pom.xml package -Dmaven.test.skip=true'

   script {
    def json = readJSON file:'config.json'
    sh 'cd ${WORKSPACE}'
    sh "chmod 400 ${json.server[0].PEM}"
    sh "scp -oStrictHostKeyChecking=no -i ${json.server[0].PEM} Development/target/${json.server[0].JARNAME} ${json.server[0].user}@${json.server[0].DNS}:${json.server[0].directory}"
    sh "ssh -oStrictHostKeyChecking=no -i ${json.server[0].PEM} ${json.server[0].user}@${json.server[0].DNS}  bash ${json.server[0].directory}/deploy.sh"
                 } //script
               }
             }
} // STAGES

 post {      
     success {
            slackSend (baseUrl: "https://northeasternuhq.slack.com/services/hooks/jenkins-ci//", token: "FxNFkRxN2MlzjDED5wv2twCS", channel: "#team-1", color: '#00FF00', message: "SUCCESSFUL: Job '${env.JOB_NAME}")
            }
   failure {  
         slackSend (baseUrl: "https://northeasternuhq.slack.com/services/hooks/jenkins-ci/", token: "FxNFkRxN2MlzjDED5wv2twCS", channel: "#team-1", color: '#FF0000', message: "FAILED: Job '${env.JOB_NAME}")
         }
   }
} //pipeline
