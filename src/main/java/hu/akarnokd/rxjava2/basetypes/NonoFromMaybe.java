/*
 * Copyright 2016-2018 David Karnok
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package hu.akarnokd.rxjava2.basetypes;

import org.reactivestreams.Subscriber;

import io.reactivex.*;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.DisposableHelper;

/**
 * Convert a MaybeSource into a Nono.
 */
final class NonoFromMaybe extends Nono {

    final MaybeSource<?> source;

    NonoFromMaybe(MaybeSource<?> source) {
        this.source = source;
    }

    @Override
    protected void subscribeActual(Subscriber<? super Void> s) {
        source.subscribe(new FromMaybeObserver(s));
    }

    static final class FromMaybeObserver extends BasicEmptyQueueSubscription
    implements MaybeObserver<Object> {

        final Subscriber<? super Void> actual;

        Disposable d;

        FromMaybeObserver(Subscriber<? super Void> actual) {
            this.actual = actual;
        }

        @Override
        public void onSubscribe(Disposable d) {
            if (DisposableHelper.validate(this.d, d)) {
                this.d = d;

                actual.onSubscribe(this);
            }
        }

        @Override
        public void onSuccess(Object value) {
            actual.onComplete();
        }

        @Override
        public void onError(Throwable t) {
            actual.onError(t);
        }

        @Override
        public void onComplete() {
            actual.onComplete();
        }

        @Override
        public void cancel() {
            d.dispose();
        }
    }
}
