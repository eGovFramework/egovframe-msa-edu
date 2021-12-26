// https://github.com/jenkinsci/kubernetes-plugin
podTemplate(
    name: "jenkins-slave",
    serviceAccount: "jenkins",
    containers: [
        containerTemplate(name: "docker", image: "docker", ttyEnabled: true, command: 'cat'),
        containerTemplate(name: "kubectl", image: "lachlanevenson/k8s-kubectl:v1.17.5", ttyEnabled: true, command: 'cat')
    ],
    volumes: [
        hostPathVolume(mountPath: "/var/run/docker.sock", hostPath: "/var/run/docker.sock")
    ]
) {
    node(POD_LABEL) {
        def now = java.time.LocalDateTime.now()
        def projectId = "discovery" // 프로젝트 아이디

        // docker
        def dockerhubCredentialsId = "username-password-dockerhub" // 젠킨스 도커허브 자격증명
        def dockerImage = "egovframe/egovframe-msa-edu-backend-${projectId}" // 도커허브 이미지
        def dockerImageTag = "latest" // 도커허브 이미지 태그

        stage("git") {
            checkout scm
        }

        stage("gradle") {
            sh "./gradlew wrap --gradle-version=7.3 --daemon"
            sh "./gradlew bootJar"
        }

        stage("docker") {
            container("docker") {
                withCredentials([usernamePassword(credentialsId: "${dockerhubCredentialsId}", passwordVariable: "password", usernameVariable: "username")]) {
                    sh "docker login -u $username -p $password"
                    sh "docker build -f DockerfileK8s -t ${dockerImage}:${dockerImageTag} ."
                    sh "docker push ${dockerImage}:${dockerImageTag}"
                }
            }
        }

        stage( "kubernetes" ) {
            container("kubectl") {
                sh "kubectl rollout restart deployment ${projectId}-deployment"
                sh "kubectl annotate deployment.apps/${projectId}-deployment kubernetes.io/change-cause='Image updated at ${now} by ${env.BUILD_TAG}.'"
            }
        }
    }
}