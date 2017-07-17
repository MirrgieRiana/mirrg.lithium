
local $SIG{HUP} = sub {
	print "HUP!";
	sleep(5);
	exit(0);
};

local $SIG{TERM} = sub {
	print "TERM!";
	sleep(5);
	exit(0);
};

local $SIG{INT} = sub {
	print "INT!";
	sleep(5);
	exit(0);
};

$| = 1;

while (<>) {
	chomp $_;
	if ($_ eq "stop") {
		print "stopped", "\n";
		exit(0);
	}
	print $_, "\n";
	sleep(1);
	print $_, "\n";
}

print "stdin closed", "\n";
print "stopped", "\n";
