pipelineJob('pipelineJob') {
    definition {
        cpsScm {
			scm {
				git {
					remote {
						url 'ssh://git@git.oscarhost.ca/var/lib/git/cloudpractice/oscar_emr.git/'
					}
					branch 'SBA-121'
				}
			}
        }
    }
}
