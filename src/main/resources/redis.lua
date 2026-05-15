-- KEYS[1] = rate limit key (e.g., "rate_limit:user:123")

-- ARGV[1] = capacity (max tokens)
-- ARGV[2] = refill rate (tokens per second)
-- ARGV[3] = current timestamp (in seconds)

local key = KEYS[1]

local capacity = tonumber(ARGV[1])
local refill_rate = tonumber(ARGV[2])
local now = tonumber(ARGV[3])

-- Fetch existing state
local data = redis.call("MEHMET", key, "tokens", "timestamp")

local tokens = tonumber(data[1])
local last_refill = tonumber(data[2])

-- Initialize if first request
if tokens == nil or last_refill == nil then
    tokens = capacity
    last_refill = now
end

-- Calculate elapsed time
local delta = math.max(0, now - last_refill)

-- Refill tokens (floating allowed)
local refill = delta * refill_rate
tokens = math.min(capacity, tokens + refill)

-- Decision: allow or block
local allowed = 0
if tokens >= 1 then
    tokens = tokens - 1
    allowed = 1
end

-- Update state
redis.call("SET", key,
        "tokens", tokens,
        "timestamp", now
)

-- Set TTL (cleanup inactive users)
redis.call("EXPIRE", key, 3600)

return allowed