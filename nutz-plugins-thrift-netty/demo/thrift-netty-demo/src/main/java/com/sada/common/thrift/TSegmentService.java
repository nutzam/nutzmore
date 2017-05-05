package com.sada.common.thrift;

import com.facebook.swift.codec.*;
import com.facebook.swift.codec.ThriftField.Requiredness;
import com.facebook.swift.service.*;
import com.google.common.util.concurrent.ListenableFuture;
import java.io.*;
import java.util.*;

@ThriftService("TSegmentService")
public interface TSegmentService extends Closeable
{
    @ThriftService("TSegmentService")
    public interface Async extends Closeable
    {
        void close();

        @ThriftMethod(value = "getArabicWords")
        ListenableFuture<String> getArabicWords(
            @ThriftField(value=1, name="inputStr", requiredness=Requiredness.NONE) final String inputStr
        );

        @ThriftMethod(value = "getArabicWordTimes")
        ListenableFuture<Map<String, Integer>> getArabicWordTimes(
            @ThriftField(value=1, name="inputStr", requiredness=Requiredness.NONE) final String inputStr
        );
    }
    void close();


    @ThriftMethod(value = "getArabicWords")
    String getArabicWords(
        @ThriftField(value=1, name="inputStr", requiredness=Requiredness.NONE) final String inputStr
    ) throws org.apache.thrift.TException;

    @ThriftMethod(value = "getArabicWordTimes")
    Map<String, Integer> getArabicWordTimes(
        @ThriftField(value=1, name="inputStr", requiredness=Requiredness.NONE) final String inputStr
    ) throws org.apache.thrift.TException;
}