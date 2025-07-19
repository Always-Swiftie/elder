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
                echo "当前分支:${GIT_TAG},当前服务:${services}"
                checkout([$class: 'GitSCM',
                          branches: [[name: GIT_TAG]],
                          doGenerateSubmoduleConfigurations: false,
                          extensions: [],
                          submoduleCfg: [],
                          userRemoteConfigs: [[credentialsId: 'server', url: GIT_URL]]
                ])
            }
        }

        // ############ 关键修改区域 ############
        // 将所有针对 zzyl-admin 的操作都放在这个 dir 块中
        stage('打包 & 部署 zzyl-admin') {
            steps {
                dir('elder/zzyl/zzyl-admin') {
                    // 1. Maven 打包
                    stage('重新Maven打包') {
                        echo "正在执行maven打包...."
                        sh "mvn clean install -DskipTests"
                    }

                    // 2. 构建镜像
                    stage('重新构建镜像') {
                        echo "当前打镜像tag:${DOCKER_TAG}"
                        // 此时已在 zzyl-admin 目录, 路径大大简化
                        // 假设 Dockerfile 就在当前目录 (elder/zzyl/zzyl-admin/Dockerfile)
                        for (ds in services.tokenize(",")) {
                            // ds 应该是 zzyl-admin
                            sh "docker build -t ${ds}:${DOCKER_TAG} ."
                        }
                    }

                    // 3. 部署服务
                    stage('部署服务'){
                        // 此时已在 zzyl-admin 目录, 路径大大简化
                        // 假设 deploy.sh 就在当前目录 (elder/zzyl/zzyl-admin/deploy.sh)
                        for (ws in services.tokenize(",")) {
                            echo "部署升级:${ws}服务"
                            sh "chmod +x ./deploy.sh && ./deploy.sh ${ws} ${DOCKER_TAG}"
                        }
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