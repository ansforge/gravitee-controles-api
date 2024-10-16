#
# (c) Copyright 2024-2024, ANS. All rights reserved.
#

 job "${nomad_jobname}" {
  namespace = "${nomad_namespace}"
  datacenters = ["${datacenter}"]
  type = "batch"

  vault {
    policies = ["checkrules", "smtp"]
  }

  parameterized {
    meta_required = ["GRAVITEE_ENVIRONMENT"]
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
        args    = ["--envid=$${NOMAD_META_GRAVITEE_ENVIRONMENT}", "--apikey=$${GRAVITEE_ENV_API_KEY}", "--recipients.filepath=local/recipients.lst"]
      }
       env {
          JAVA_TOOL_OPTIONS = "-Dspring.config.location=classpath:/application.properties,file:/secrets/application.properties -Xms256m -Xmx256m -XX:+UseG1GC"
       }
      dispatch_payload {
        file = "recipients.lst"
      }
      template {
        data = <<EOT
logging.level.root=INFO
apim.management.url={{ range service "gravitee-apim-management-api" }}http://{{.Address}}:{{Port}}{{end}}/management
spring.mail.host={{ with secret "services-infrastructure/smtp" }}{{.Data.data.host}}{{end}}
spring.mail.port={{ with secret "services-infrastructure/smtp" }}{{.Data.data.port}}{{end}}
        EOT
        destination = "secrets/application.properties"
      }
      template {
        data = <<EOT
GRAVITEE_ENV_API_KEY = {{ with secret "checkrules/gravitee" }}{{.Data.data.gravitee_api_key}}{{end}}
        EOT
        destination = "secrets/.env"
        env = true
      }
    }
  }
}
