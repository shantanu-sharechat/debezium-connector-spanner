/*
 * Copyright Debezium Authors.
 *
 * Licensed under the Apache Software License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package io.debezium.connector.spanner.db.metadata;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import com.google.cloud.Timestamp;
import com.google.cloud.spanner.AsyncResultSet;
import com.google.cloud.spanner.DatabaseClient;
import com.google.cloud.spanner.ForwardingAsyncResultSet;
import com.google.cloud.spanner.ReadOnlyTransaction;
import com.google.cloud.spanner.SpannerException;

import io.debezium.connector.spanner.db.dao.SchemaDao;

class SchemaRegistryTest {

    @Test
    void testInit() throws SpannerException {
        AsyncResultSet asyncResultSet = mock(AsyncResultSet.class);
        when(asyncResultSet.getBoolean(anyInt())).thenReturn(true);
        when(asyncResultSet.getString(anyInt())).thenReturn("String");
        when(asyncResultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);

        ReadOnlyTransaction readOnlyTransaction = mock(ReadOnlyTransaction.class);
        when(readOnlyTransaction.executeQuery(any(), any()))
                .thenReturn(new ForwardingAsyncResultSet(new ForwardingAsyncResultSet(new ForwardingAsyncResultSet(
                        new ForwardingAsyncResultSet(new ForwardingAsyncResultSet(asyncResultSet))))));
        doNothing().when(readOnlyTransaction).close();

        DatabaseClient databaseClient = mock(DatabaseClient.class);
        when(databaseClient.readOnlyTransaction(any())).thenReturn(readOnlyTransaction);

        SchemaRegistry schemaRegistry = new SchemaRegistry("Stream Name", new SchemaDao(databaseClient), mock(Runnable.class));
        schemaRegistry.init(Timestamp.ofTimeMicroseconds(1L));

        verify(databaseClient, atLeast(1)).readOnlyTransaction(any());
        verify(readOnlyTransaction, atLeast(1)).executeQuery(any(), any());
        verify(readOnlyTransaction, atLeast(1)).close();
        verify(asyncResultSet, atLeast(1)).next();
        verify(asyncResultSet, atLeast(1)).getBoolean(anyInt());
        assertTrue(schemaRegistry.getAllTables().isEmpty());
    }

    @Test
    void testGetWatchedTable() {
        SchemaRegistry schemaRegistry = new SchemaRegistry("Stream Name", new SchemaDao(mock(DatabaseClient.class)), mock(Runnable.class));
        assertThrows(IllegalStateException.class,
                () -> schemaRegistry.getWatchedTable(TableId.getTableId("Table Name")));
    }

    @Test
    void testGetAllTables() {
        assertThrows(IllegalStateException.class, () -> (new SchemaRegistry("Stream Name",
                new SchemaDao(mock(DatabaseClient.class)), mock(Runnable.class))).getAllTables());
    }

    @Test
    void testCheckSchema() {
        SchemaRegistry schemaRegistry = new SchemaRegistry("Stream Name", new SchemaDao(mock(DatabaseClient.class)), mock(Runnable.class));
        TableId tableId = TableId.getTableId("Table Name");
        Timestamp timestamp = Timestamp.ofTimeMicroseconds(1L);
        assertThrows(IllegalStateException.class,
                () -> schemaRegistry.checkSchema(tableId, timestamp, new ArrayList<>()));
    }

    @Test
    void testUpdateAndPublish() throws SpannerException {
        AsyncResultSet asyncResultSet = mock(AsyncResultSet.class);
        when(asyncResultSet.getBoolean(anyInt())).thenReturn(true);
        when(asyncResultSet.getString(anyInt())).thenReturn("String");
        when(asyncResultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);

        ReadOnlyTransaction readOnlyTransaction = mock(ReadOnlyTransaction.class);
        when(readOnlyTransaction.executeQuery(any(), any()))
                .thenReturn(new ForwardingAsyncResultSet(new ForwardingAsyncResultSet(new ForwardingAsyncResultSet(
                        new ForwardingAsyncResultSet(new ForwardingAsyncResultSet(asyncResultSet))))));
        doNothing().when(readOnlyTransaction).close();

        DatabaseClient databaseClient = mock(DatabaseClient.class);
        when(databaseClient.readOnlyTransaction(any())).thenReturn(readOnlyTransaction);
        SchemaDao schemaDao = new SchemaDao(databaseClient);

        SchemaRegistry schemaRegistry = new SchemaRegistry("Stream Name", schemaDao, mock(Runnable.class));
        schemaRegistry.updateSchema(Timestamp.ofTimeMicroseconds(1L));

        verify(databaseClient, atLeast(1)).readOnlyTransaction(any());
        verify(readOnlyTransaction, atLeast(1)).executeQuery(any(), any());
        verify(readOnlyTransaction, atLeast(1)).close();
        verify(asyncResultSet, atLeast(1)).next();
        verify(asyncResultSet, atLeast(1)).getBoolean(anyInt());

        assertTrue(schemaRegistry.getAllTables().isEmpty());
    }

    @Test
    void testUpdateSchema() throws SpannerException {
        AsyncResultSet asyncResultSet = mock(AsyncResultSet.class);
        when(asyncResultSet.getBoolean(anyInt())).thenReturn(true);
        when(asyncResultSet.getString(anyInt())).thenReturn("String");
        when(asyncResultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);

        ReadOnlyTransaction readOnlyTransaction = mock(ReadOnlyTransaction.class);
        when(readOnlyTransaction.executeQuery(any(), any()))
                .thenReturn(new ForwardingAsyncResultSet(new ForwardingAsyncResultSet(new ForwardingAsyncResultSet(
                        new ForwardingAsyncResultSet(new ForwardingAsyncResultSet(asyncResultSet))))));
        doNothing().when(readOnlyTransaction).close();

        DatabaseClient databaseClient = mock(DatabaseClient.class);
        when(databaseClient.readOnlyTransaction(any())).thenReturn(readOnlyTransaction);

        SchemaRegistry schemaRegistry = new SchemaRegistry("Stream Name", new SchemaDao(databaseClient), mock(Runnable.class));
        assertTrue(schemaRegistry.updateSchema(Timestamp.ofTimeMicroseconds(1L)));

        verify(databaseClient, atLeast(1)).readOnlyTransaction(any());
        verify(readOnlyTransaction, atLeast(1)).executeQuery(any(), any());
        verify(readOnlyTransaction, atLeast(1)).close();
        verify(asyncResultSet, atLeast(1)).next();
        verify(asyncResultSet, atLeast(1)).getBoolean(anyInt());
        assertTrue(schemaRegistry.getAllTables().isEmpty());
    }
}