package importnew.importnewclient.net;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;

/**
 * Created by Xingfeng on 2016/5/1.
 */
public class Test {

    public static void main(String[] args) {


        Observable<Integer> objectObservable = Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {

                for (int i = 0; i <5;i++){
                    subscriber.onNext(i);
                }
                subscriber.onCompleted();
            }
        });

        Subscription subscriptionPrint=objectObservable.subscribe(new Observer<Integer>() {
            @Override
            public void onCompleted() {
                System.out.println("Observable completed");
            }

            @Override
            public void onError(Throwable e) {
                System.out.println(e);
            }

            @Override
            public void onNext(Integer integer) {
                System.out.println(integer.toString());
            }
        });

    }

}
