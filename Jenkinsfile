/**
 * (c) Copyright 2024-2024, ANS. All rights reserved.
 */
@Library('AnsPipeline') _

env.MAIL_TMA = "lionel.poma.ext@esante.gouv.fr"
AnsPipelineJdkMvn(
    svnCredentialsId:"${env.GITHUB_TOKEN}",
    mailList:"${env.MAIL_TMA}",
    haveFrontModule: "false",
    mvnOpts: "",
    git:"true",
    isDockerDaemon: "true",
    projectName:  "fr-gouv-esante-apim-checkrules",
    applicationName: ['apim-rules-checker'],
    trivyParams: "--ignore-unfixed",
    trivySeverity: "HIGH,CRITICAL,MEDIUM,LOW,UNKNOWN",
    pathOfDockerfile: "."
)

