package managers;

@FunctionalInterface
public interface BrowserUseInstance<T, W>{
    void accept(T driver, W wait) throws InterruptedException;
}
