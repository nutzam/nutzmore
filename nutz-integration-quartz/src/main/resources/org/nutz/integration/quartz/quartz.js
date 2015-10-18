var ioc = {
   "scheduler" :{
      "type" :"org.quartz.Scheduler",
      "factory" :"org.quartz.impl.StdSchedulerFactory#getDefaultScheduler",
      "events" :{
         "create" :"start",
         "depose" :"shutdown"
      },
      "fields" :{
         "jobFactory" :{
            "refer" :"jobFactory"
         }
      }
   },
   "jobFactory" :{
      "type" :"org.nutz.integration.quartz.NutQuartzJobFactory",
      "args" :[{
         "refer" :"$ioc"
      }]
   },
   "nutQuartzCronJobFactory" :{
      "type" :"org.nutz.integration.quartz.NutQuartzCronJobFactory",
      "events" :{
         "create" :"init"
      },
      "fields" :{
         "scheduler" :{
            "refer" :"scheduler"
         },
         "conf" :{
            "refer" :"conf"
         }
      }
   }
};