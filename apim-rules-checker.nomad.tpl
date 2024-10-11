#
# (c) Copyright 2024-2024, ANS. All rights reserved.
#

 job "${nomad_jobname}" {
  namespace = "${nomad_namespace}"
  datacenters = ["${datacenter}"]
  type = "batch"

  parameterized {
    meta_required = ["GRAVITEE_ENVIRONMENT", "GRAVITEE_ENV_API_KEY"]
    payload       = "required"
  }
  
  periodic {
    cron = "0 3 * * * *"
    prohibit_overlap = true
  }
    
  group "check-apis-definitions" {
    task "check-apis-definitions" {
      driver = "docker"
      config {
        image   = "${image}:${tag}"
        args    = ["--envid=$${NOMAD_META_GRAVITEE_ENVIRONMENT}", "--apikey=$${NOMAD_META_GRAVITEE_ENV_API_KEY}", "--recipients.filepath=local/recipients.lst"]
      }
       env {
          JAVA_TOOL_OPTIONS = "-Dspring.config.location=/secrets/application.properties -Xms256m -Xmx256m -XX:+UseG1GC"
       }
      dispatch_payload {
        file = "recipients.lst"
      }
      template {
        data = <<EOT
logging.level.root=INFO
      EOT
        destination = "secrets/application.properties"
      }
    }
  }
}
