### Primary Bean

When you have byType in auto wire
and there are two beans
and spring is not able to select

use primary="true"
and that bean will be created

And it only works when there is no property tag to decide which bean have to refer

