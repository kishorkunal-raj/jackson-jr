package com.fasterxml.jackson.jr.ob.impl;

import java.io.IOException;
import java.util.*;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.jr.ob.JSONObjectException;
import com.fasterxml.jackson.jr.ob.api.MapBuilder;
import com.fasterxml.jackson.jr.ob.api.ValueReader;

/**
 * Reader for typed {@link java.util.Map} values.
 */
public class MapReader extends ValueReader
{
    protected final Class<?> _mapType;
    protected final ValueReader _valueReader;

    public MapReader(Class<?> t, ValueReader vr) {
        // Some caveats: drop type if it's generic enough (aka "don't care")
        _mapType = (t == Map.class) ? null : t;
        _valueReader = vr;
    }

    @Override
    public Object readNext(JSONReader r, JsonParser p) throws IOException {
        if (p.nextToken() != JsonToken.START_OBJECT) {
            if (p.hasToken(JsonToken.VALUE_NULL)) {
                return null;
            }
            throw JSONObjectException.from(p, "Unexpected token %s; should get START_OBJECT",
                    p.currentToken());
        }
        
        MapBuilder b = r._mapBuilder(_mapType);
        String propName0 = p.nextFieldName();
        if (propName0 == null) {
            if (p.hasToken(JsonToken.END_OBJECT)) {
                return b.emptyMap();
            }
            throw _reportWrongToken(p);
        }
        Object value = _valueReader.readNext(r, p);
        String propName = p.nextFieldName();
        if (propName == null) {
            if (p.hasToken(JsonToken.END_OBJECT)) {
                return b.singletonMap(propName0, value);
            }
            throw _reportWrongToken(p);
        }
        try {
            b = b.start().put(propName0, value);
            while (true) {
                b = b.put(propName, _valueReader.readNext(r, p));
                propName = p.nextFieldName();
                if (propName == null) {
                    if (p.hasToken(JsonToken.END_OBJECT)) {
                        return b.build();
                    }
                    throw _reportWrongToken(p);
                }
            }
        } catch (IllegalArgumentException e) {
            throw JSONObjectException.from(p, e.getMessage());
        }
    }

    @Override
    public Object read(JSONReader r, JsonParser p) throws IOException {
        MapBuilder b = r._mapBuilder(_mapType);
        String propName0 = p.nextFieldName();
        if (propName0 == null) {
            if (p.hasToken(JsonToken.END_OBJECT)) {
                return b.emptyMap();
            }
            throw _reportWrongToken(p);
        }
        Object value = _valueReader.readNext(r, p);
        String propName = p.nextFieldName();
        if (propName == null) {
            if (p.hasToken(JsonToken.END_OBJECT)) {
                return b.singletonMap(propName0, value);
            }
            throw _reportWrongToken(p);
        }
        try {
            b = b.start().put(propName0, value);
            while (true) {
                b = b.put(propName, _valueReader.readNext(r, p));
                propName = p.nextFieldName();
                if (propName == null) {
                    if (p.hasToken(JsonToken.END_OBJECT)) {
                        return b.build();
                    }
                    throw _reportWrongToken(p);
                }
            }
        } catch (IllegalArgumentException e) {
            throw JSONObjectException.from(p, e.getMessage());
        }
    }

    protected JSONObjectException _reportWrongToken(JsonParser p) {
        return JSONObjectException.from(p, "Unexpected token %s; should get FIELD_NAME or END_OBJECT",
                p.currentToken());
    }
}
