pipeline {
    agent any
    options {
        timestamps() // 在控制台输出中显示时间戳
    }
    // 注意：parameters 块是 Jenkinsfile 自身的定义，
    // 如果你在 Jenkins UI 的“参数化构建过程”中已经定义了这些参数，
    // 那么这里就不用重复定义了，但为了代码的完整性和可读性，
    // 我在此处保留了参数定义，你可以根据实际情况决定是否需要移除。
    // 如果Jenkins UI已经定义，这里重复定义可能会导致警告或覆盖。
    parameters {
        string(name: 'service', defaultValue: 'zzyl-admin', description: 'The specific service directory to build and deploy (e.g., zzyl-admin)')
        string(name: 'GIT_URL', defaultValue: 'git@github.com:Always-Swiftie/elder.git', description: 'Git repository URL (SSH format)')
        string(name: 'GIT_TAG', defaultValue: 'master', description: 'Git branch or tag to checkout (e.g., master, main, v1.0)')
        string(name: 'DOCKER_TAG', defaultValue: 'latest', description: 'Docker image tag (e.g., latest, v1.0.0)')
    }
    tools { // 配置 Jenkins 使用的工具链
        maven 'maven' // 确保在 Jenkins 全局工具配置中已设置名为 'maven' 的 Maven
        jdk 'jdk11'   // 确保在 Jenkins 全局工具配置中已设置名为 'jdk11' 的 JDK
    }
    stages {
        stage('清除工作空间') {
            steps {
                cleanWs() // 清理当前 Jenkins 工作空间
            }
        }
        stage('拉取Git代码') {
            steps {
                echo "正在拉取代码..."
                echo "当前分支: ${params.GIT_TAG}, 目标服务目录: ${params.service}"
                checkout([$class: 'GitSCM',
                          branches: [[name: params.GIT_TAG]], // 根据参数拉取分支
                          doGenerateSubmoduleConfigurations: false,
                          extensions: [],
                          submoduleCfg: [],
                          // credentialsId: 'server' 替换为你实际在 Jenkins 中创建的 SSH 凭据的ID
                          userRemoteConfigs: [[credentialsId: 'server', url: params.GIT_URL]] // <-- 使用 params.GIT_URL
                ])
                sh "pwd" // 验证当前工作目录，此时是 /var/jenkins_home/workspace/zzyl-admin
            }
        }
        stage('进入服务目录并Maven打包') { // 阶段名称更具描述性
            steps {
                echo "正在进入 zzyl/${params.service} 目录并执行Maven打包...."
                script {
                    // 使用 'dir' 命令进入到实际包含 pom.xml 的服务目录
                    dir("zzyl/${params.service}") { // <-- 使用 params.service
                        sh "pwd" // 验证当前目录，此时应是 /var/jenkins_home/workspace/zzyl-admin/zzyl/zzyl-admin
                        sh "mvn clean install -DskipTests" // 执行 Maven 打包命令
                    }
                }
            }
        }
        stage('重新构建Docker镜像') {
            steps {
                echo "当前Docker镜像标签: ${params.DOCKER_TAG}"
                script {
                    // 再次进入服务目录，因为 Dockerfile 和打包好的 JAR 文件都在这里
                    dir("zzyl/${params.service}") { // <-- 使用 params.service
                        echo "在 ${params.service} 目录执行Docker镜像打包......"
                        // -t ${params.service}:${params.DOCKER_TAG}: 使用服务名和 Docker 标签命名镜像
                        // -f Dockerfile .: 指定 Dockerfile 在当前目录，构建上下文为当前目录
                        sh "docker build -t ${params.service}:${params.DOCKER_TAG} -f Dockerfile ."
                    }
                }
            }
        }
        stage('部署服务'){
            steps {
                script {
                    // 进入服务目录，因为 deploy.sh 在这里
                    dir("zzyl/${params.service}") { // <-- 使用 params.service
                        echo "部署升级: ${params.service} 服务"
                        // 赋予 deploy.sh 执行权限并运行它，传递服务名和Docker标签作为参数
                        sh "chmod +x ./deploy.sh && sh ./deploy.sh ${params.service} ${params.DOCKER_TAG}"
                    }
                }
            }
        }
    }
    post { // 后置操作，无论构建结果如何都会执行
        always {
            echo '--- 任务构建完毕 ---'
        }
        // 以下是可选的错误/成功通知示例
        failure {
            echo '--- 任务构建失败！请检查日志 ---'
            // 可以添加邮件通知或IM通知，例如：
            // mail to: 'your_email@example.com',
            //      subject: "Jenkins Job '${env.JOB_NAME}' Failed",
            //      body: "Build #${env.BUILD_NUMBER} failed. See ${env.BUILD_URL}"
        }
        success {
            echo '--- 任务构建成功！ ---'
        }
    }
}