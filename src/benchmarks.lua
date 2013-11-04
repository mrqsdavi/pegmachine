
local lpeg = require"lpeg"

local bible = ""

file = io.open("bible.txt", "r")
if file ~= nil then
	bible = file:read("*all")
else
    print("Não encontrou arquivo")
end

local any = lpeg.P(1)
function search (p)
	local sch = lpeg.P{ lpeg.P(p) + any * lpeg.V(1) }
	local res = lpeg.match(sch, bible) 
	if res then res = res - string.len(p) end
	return res
end

--[[local any = lpeg.P(1)
--function search (p)
	--local x = string.sub(p, 1, 1)
	--local sch = lpeg.P{ p + any * (any - x)^0 * lpeg.V(1) }
	--return lpeg.match(sch, bible)-string.len(p)
--end
--]]

function benchmark (p)
	local startTime = os.clock()*1000
	local position = search (p)
	local endTime = os.clock()*1000
	print(string.format("%s: %d - %fms", p, position ,(endTime - startTime)))
end

--benchmark("@the")
benchmark("Omega")
benchmark("Alpha")
benchmark("amethyst")
benchmark("amethysts")
--benchmark("heith")
--benchmark("eartt")