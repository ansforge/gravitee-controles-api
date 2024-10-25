#
# (c) Copyright 2024-2024, ANS. All rights reserved.
#

project = "${workspace.name}"

labels = { "domaine" = "gravitee" }

runner {
  enabled = true
  data_source "git" {
    url                         = "https://github.com/ansforge/gravitee-controles-api.git"
    ref                         = "gitref"
    ignore_changes_outside_path = true
  }
  poll {
    enabled = false
  }
}

# An application to deploy.
app "apim-rules-checker" {
  # Build specifies how an application should be deployed.
  build {
    use "docker-pull" {
      image              = var.image
      tag                = var.tag
      disable_entrypoint = true
    }
  }

  # Deploy to Nomad
  deploy {
    use "nomad-jobspec" {
      jobspec = templatefile("${path.app}/apim-rules-checker.nomad.tpl", {
        image           = var.image
        tag             = var.tag
        datacenter      = var.datacenter
        nomad_namespace = "${workspace.name}"
        cron_expression = var.cron_expression
      })
    }
  }
}

variable cron_expression {
  type    = string
  default = "0 3 * * * * "
}

variable datacenter {
  type    = string
  default = "*"
  env = ["NOMAD_DC"]
}

variable "image" {
  type    = string
  default = "ans/fr-gouv-esante-apim-checkrules/apim-rules-checker"
}

variable "tag" {
  type    = string
  default = "0.0.2-SNAPSHOT"
}