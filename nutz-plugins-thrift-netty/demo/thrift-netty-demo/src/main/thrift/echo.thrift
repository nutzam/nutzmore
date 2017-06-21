#!/usr/local/bin/thrift --gen cpp:pure_enums --gen php

namespace java org.nutz.plugins.thrift.netty.demo.api

service Echo
{
  string echo(1: string info)
}
