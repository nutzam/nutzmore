var ioc = {
   "thriftFactory" :{
      "type" :"org.nutz.plugins.thrift.NutThriftNettyFactory",
      "args" :[{
         "refer" :"$ioc"
      }]
   }
};