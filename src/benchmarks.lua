
local lpeg = require"lpeg"
local re = require"re"

local bible = ""

file = io.open("bible.txt", "r")
if file ~= nil then
	bible = file:read("*all")
else
    print("Não encontrou arquivo")
end

local any = lpeg.P(1)
function searchP (p)
	local sch = lpeg.P{ lpeg.P(p) + any * lpeg.V(1) }
	local res = lpeg.match(sch, bible) 
	if res==nil then
		res = 0
	end
	return res
end

function searchG (p)
	local res = lpeg.match(p, bible)
	if res==nil then
		res = 0
	end
	return res
end

function searchW (p)
	local x = string.sub(p, 1, 1)
	local sch = lpeg.P{ p + any * (any - x)^0 * lpeg.V(1) }
	local res = lpeg.match(sch, bible)
	if res==nil then
		res = 0
	end
	return res
end

function benchmark (p, flag)
	local startTime = os.clock()*1000
	local position = 0
	
	if flag==0 then
		position = searchW (p)
	elseif flag==1 then
		position = searchP (p)
	else
		position = searchG (p)
	end
	
	local endTime = os.clock()*1000
	print(string.format("%s: %d - %fms", p, position ,(endTime - startTime)))
end

print ("Busca com otimização")
benchmark("@the",0)
benchmark("Omega",0)
benchmark("Alpha",0)
benchmark("amethysts",0)
benchmark("heith",0)
benchmark("eartt",0)

print ("\nBusca sem otimização")
benchmark("@the",1)
benchmark("Omega",1)
benchmark("Alpha",1)
benchmark("amethysts",1)
benchmark("heith",1)
benchmark("eartt",1)
benchmark("[A-Za-z ]*", 1)
benchmark("([a-zA-Z]+'Abram')", 1)
benchmark("([a-zA-Z]+'Joseph')",1)

print ("\nEffectiveness of optimizations");
benchmark("S <- 'transparent' / . <S>", 2);

local p = lpeg.P{
	"S",
	S = lpeg.P"transparent" + lpeg.P(1) * lpeg.V"S"
}
benchmark(p, 2)

benchmark("(!'transparent' .)* 'transparent'", 2);
benchmark("S <- [a-zA-Z]+ ' '* 'transparent' / . S", 2);
benchmark("(!([a-zA-Z]+ ' '* 'transparent') .)*", 2);