import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class RateLimitingFilter implements Filter {
    private static final int MAX_ATTEMPTS = 5;
    private static final long TIME_WINDOW = TimeUnit.MINUTES.toMillis(15); // 15 minutes

    private final Map<String, Attempt> attempts = new HashMap<>();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String ipAddress = httpRequest.getRemoteAddr();

        synchronized (attempts) {
            Attempt attempt = attempts.get(ipAddress);
            long currentTime = System.currentTimeMillis();

            if (attempt == null) {
                attempt = new Attempt(1, currentTime);
                attempts.put(ipAddress, attempt);
            } else {
                if (currentTime - attempt.timestamp > TIME_WINDOW) {
                    attempt.count = 1;
                    attempt.timestamp = currentTime;
                } else {
                    attempt.count++;
                }
            }

            if (attempt.count > MAX_ATTEMPTS) {
                response.getWriter().write("Too many login attempts. Please try again later.");
                return;
            }
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }

    private static class Attempt {
        int count;
        long timestamp;

        Attempt(int count, long timestamp) {
            this.count = count;
            this.timestamp = timestamp;
        }
    }
}
