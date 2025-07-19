pipeline {
    agent any
    options {
        timestamps()
    }
    tools {
        maven 'maven'
        jdk 'jdk11'
    }
    stages {
        stage('清除工作空间') {
            steps {
                cleanWs()
            }
        }
        stage('拉取Git代码') {
            steps {
                echo "正在拉取代码..."
                echo "当前分支:${GIT_TAG},当前服务:${service}"
                checkout([$class: 'GitSCM',
                          branches: [[name: GIT_TAG]],
                          doGenerateSubmoduleConfigurations: false,
                          extensions: [],
                          submoduleCfg: [],
                          userRemoteConfigs: [[credentialsId: 'server', url: GIT_URL]] // <--- HERE: Add single quotes around the URL
                ])
                sh "pwd"
            }
        }
    stage('重新Maven打包') {
        steps {
            echo "正在执行maven打包...."
            script {
            // 假设你的 pom.xml 在 'your-sub-module' 目录下
            // 例如：如果你的项目是单模块，但 pom.xml 在 'admin' 目录下，则写 'admin'
            // 如果是多模块项目，且父 pom 在根目录，子模块在 'service-a' 目录下，
            // 那么你可以在父 pom 所在的根目录执行 mvn，或者 cd 到子模块目录单独打包。
            // 这里我们假设你需要进入某个子目录才能找到 pom.xml
                dir('zzyl-admin') { // <-- 添加 dir 块，进入包含 pom.xml 的子目录
                    sh "mvn clean install -DskipTests"
            }
        }
    }
}
        stage('重新构建镜像') {
            steps {
                echo "当前打镜像tag:${DOCKER_TAG}"
                script {
                    for (ds in service.tokenize(",")) {
                         sh "pwd"
                         echo "进入target目录执行镜像打包......"
                         sh "cd ./${ds}/target/ && docker build -t ${ds}:${DOCKER_TAG} -f ../Dockerfile ."
                    }
                }
            }
        }
        stage('部署服务'){
            steps {
                script {
                    for (ws in service.tokenize(",")) {
                        sh "pwd"
                        sh "cd `pwd`"
                        echo "部署升级:${ws}服务"
                        sh "chmod +x ./${ws}/deploy.sh && sh ./${ws}/deploy.sh ${ws} ${DOCKER_TAG}"
                    }
                }
            }
        }

    }
    post {
        always {
            echo '任务构建完毕'
        }
    }
}