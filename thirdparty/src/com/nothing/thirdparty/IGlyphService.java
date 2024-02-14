/*
 * Copyright (C) 2024 Paranoid Android
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.nothing.thirdparty;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public interface IGlyphService extends IInterface {

    public static final String DESCRIPTOR = "com.nothing.thirdparty.IGlyphService";

    void setFrameColors(int[] colors) throws RemoteException;
    void openSession() throws RemoteException;
    void closeSession() throws RemoteException;
    boolean register(String key) throws RemoteException;
    boolean registerSDK(String key, String device) throws RemoteException;

    public static class Default implements IGlyphService {
        @Override
        public void setFrameColors(int[] colors) throws RemoteException { }

        @Override
        public void openSession() throws RemoteException { }

        @Override
        public void closeSession() throws RemoteException { }

        @Override
        public boolean register(String key) throws RemoteException {
            return false;
        }

        @Override
        public boolean registerSDK(String key, String device) throws RemoteException {
            return false;
        }

        @Override
        public IBinder asBinder() {
            return null;
        }
    }

    public static abstract class Stub extends Binder implements IGlyphService {
        static final int TRANSACTION_setFrameColors = 1;
        static final int TRANSACTION_openSession = 2;
        static final int TRANSACTION_closeSession = 3;
        static final int TRANSACTION_register = 4;
        static final int TRANSACTION_registerSDK = 5;

        public Stub() {
            attachInterface(this, IGlyphService.DESCRIPTOR);
        }

        public static IGlyphService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IGlyphService.DESCRIPTOR);
            if (iin != null && (iin instanceof IGlyphService)) {
                return (IGlyphService) iin;
            }
            return new Proxy(obj);
        }

        @Override
        public IBinder asBinder() {
            return this;
        }

        @Override
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            if (code >= TRANSACTION_setFrameColors && code <= 16777215) {
                data.enforceInterface(IGlyphService.DESCRIPTOR);
            }
            switch (code) {
                case 1598968902:
                    reply.writeString(IGlyphService.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case TRANSACTION_setFrameColors:
                            int[] colors = data.createIntArray();
                            data.enforceNoDataAvail();
                            setFrameColors(colors);
                            reply.writeNoException();
                            return true;
                        case TRANSACTION_openSession:
                            openSession();
                            reply.writeNoException();
                            return true;
                        case TRANSACTION_closeSession:
                            closeSession();
                            reply.writeNoException();
                            return true;
                        case TRANSACTION_register:
                            String arg = data.readString();
                            data.enforceNoDataAvail();
                            boolean result = register(arg);
                            reply.writeNoException();
                            reply.writeBoolean(result);
                            return true;
                        case TRANSACTION_registerSDK:
                            String arg1 = data.readString();
                            String arg2 = data.readString();
                            data.enforceNoDataAvail();
                            boolean resultSdk = registerSDK(arg1, arg2);
                            reply.writeNoException();
                            reply.writeBoolean(resultSdk);
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        private static class Proxy implements IGlyphService {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IGlyphService.DESCRIPTOR;
            }

            @Override
            public void setFrameColors(int[] colors) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IGlyphService.DESCRIPTOR);
                    _data.writeIntArray(colors);
                    this.mRemote.transact(Stub.TRANSACTION_setFrameColors, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override
            public void openSession() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IGlyphService.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_openSession, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override
            public void closeSession() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IGlyphService.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_closeSession, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override
            public boolean register(String key) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IGlyphService.DESCRIPTOR);
                    _data.writeString(key);
                    this.mRemote.transact(Stub.TRANSACTION_register, _data, _reply, 0);
                    _reply.readException();
                    return _reply.readBoolean();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override
            public boolean registerSDK(String key, String device) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IGlyphService.DESCRIPTOR);
                    _data.writeString(key);
                    _data.writeString(device);
                    this.mRemote.transact(Stub.TRANSACTION_registerSDK, _data, _reply, 0);
                    _reply.readException();
                    return _reply.readBoolean();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }
    }
}