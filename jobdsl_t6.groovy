job('github_t6'){
  	scm {
    		github('smc181002/react-hello')
  	}
  	triggers {
    		githubPush()
  	}
  	steps {
    		shell("cp -rf * /home")
  	}
}

job('find_code_t6_dsl'){
  triggers{
    upstream('github_t6')
  }	
  	steps{
      		shell("""if [[ `ls /home/ | grep .*'.html'` ]]
        		then 
          			kubectl apply -f https://raw.githubusercontent.com/smc181002/devops-task-3-files/master/deploy-web-test.yml
        		else 
          			echo pyhton
        		fi""")
  	}
}

job('test_code_t6_dsl'){
	triggers{
		upstream('find_code_t6_dsl')
	}	
  	steps {
 		shell("""if [[ `curl -s -o /dev/null -w "%{http_code}" 192.168.99.100:31000` -ne 200 ]]
            then
              echo ERROR with status code: `curl -s -o /dev/null -w "%{http_code}" 192.168.99.100:31000`
              exit 1
            else
              echo "running fine"
              exit 0
            fi""")
  	}
  	publishers {
    		mailer("meherchaitanya18802@gmail.com")
  	}
}

job('launch_prod_t6_dsl'){
  	triggers{
    		upstream('test_code_t6_dsl')
  	}	
  	steps {
 		shell("kubectl apply -f https://raw.githubusercontent.com/smc181002/devops-task-3-files/master/deploy-web.yml")
  	}
}

buildPipelineView('devops_t6_dsl') {
    	filterBuildQueue()
    	filterExecutors()
    	title('pipeline_dsl_gen')
    	displayedBuilds(5)
    	selectedJob('github_t6')
    	alwaysAllowManualTrigger()
    	showPipelineParameters()
    	refreshFrequency(60)
}
