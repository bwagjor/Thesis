///////////////////////////////////////////////////////////////////////////////
// FILE:          ASILED.cpp
// PROJECT:       Micro-Manager
// SUBSYSTEM:     DeviceAdapters
//-----------------------------------------------------------------------------
// DESCRIPTION:   ASI LED shutter device adapter
//
// COPYRIGHT:     Applied Scientific Instrumentation, Eugene OR
//
// LICENSE:       This file is distributed under the BSD license.
//
//                This file is distributed in the hope that it will be useful,
//                but WITHOUT ANY WARRANTY; without even the implied warranty
//                of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
//
//                IN NO EVENT SHALL THE COPYRIGHT OWNER OR
//                CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
//                INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES.
//
// AUTHOR:        Jon Daniels (jon@asiimaging.com) 05/2014
//
// BASED ON:      ASIStage.cpp and others
//

#ifdef WIN32
#define snprintf _snprintf 
#pragma warning(disable: 4355)
#endif

#include "ASILED.h"
#include "ASITiger.h"
#include "ASIHub.h"
#include "../../MMDevice/ModuleInterface.h"
#include "../../MMDevice/DeviceUtils.h"
#include "../../MMDevice/DeviceBase.h"
#include "../../MMDevice/MMDevice.h"
#include <iostream>
#include <cmath>
#include <sstream>
#include <string>
#include <vector>

using namespace std;


///////////////////////////////////////////////////////////////////////////////
// CLED
//
CLED::CLED(const char* name) :
   ASIPeripheralBase< ::CShutterBase, CLED >(name),
   open_(false),
   intensity_(50)
{
   // no body needed
}

int CLED::Initialize()
{
   // call generic Initialize first, this gets hub
   RETURN_ON_MM_ERROR( PeripheralInitialize() );

   // create MM description; this doesn't work during hardware configuration wizard but will work afterwards
   ostringstream command;
   command.str("");
   command << g_LEDDeviceDescription << " HexAddr=" << addressString_;
   CreateProperty(MM::g_Keyword_Description, command.str().c_str(), MM::String, true);

   CPropertyAction* pAct;

   pAct = new CPropertyAction (this, &CLED::OnIntensity);
   CreateProperty(g_LEDIntensityPropertyName, "50", MM::Integer, false, pAct);
   SetPropertyLimits(g_LEDIntensityPropertyName, 1, 100);
   UpdateProperty(g_LEDIntensityPropertyName);  // this takes care of initializing open_ and intensity_

   pAct = new CPropertyAction (this, &CLED::OnState);
   CreateProperty(g_ShutterState, g_OpenState, MM::String, false, pAct);
   AddAllowedValue(g_ShutterState, g_OpenState);
   AddAllowedValue(g_ShutterState, g_ClosedState);
   UpdateProperty(g_ShutterState);

   // refresh properties from controller every time; default is false = no refresh (speeds things up by not redoing so much serial comm)
   pAct = new CPropertyAction (this, &CLED::OnRefreshProperties);
   CreateProperty(g_RefreshPropValsPropertyName, g_NoState, MM::String, false, pAct);
   AddAllowedValue(g_RefreshPropValsPropertyName, g_NoState);
   AddAllowedValue(g_RefreshPropValsPropertyName, g_YesState);

   // save settings to controller if requested
   pAct = new CPropertyAction (this, &CLED::OnSaveCardSettings);
   CreateProperty(g_SaveSettingsPropertyName, g_SaveSettingsOrig, MM::String, false, pAct);
   AddAllowedValue(g_SaveSettingsPropertyName, g_SaveSettingsX);
   AddAllowedValue(g_SaveSettingsPropertyName, g_SaveSettingsY);
   AddAllowedValue(g_SaveSettingsPropertyName, g_SaveSettingsZ);
   AddAllowedValue(g_SaveSettingsPropertyName, g_SaveSettingsOrig);
   AddAllowedValue(g_SaveSettingsPropertyName, g_SaveSettingsDone);

   initialized_ = true;
   return DEVICE_OK;
}



int CLED::SetOpen(bool open)
{
   ostringstream command; command.str("");
   if(open)
   {
      command << addressChar_ << "LED X=" << intensity_;
   }
   else
   {
      command << addressChar_ << "LED X=0";
   }
   RETURN_ON_MM_ERROR( hub_->QueryCommandVerify(command.str(), ":A"));
   open_ = open;
   return DEVICE_OK;
}

int CLED::GetOpen(bool& open)
// returns the cached value instead of querying controller itself
{
   open = open_;
   return DEVICE_OK;
}

int CLED::UpdateOpenIntensity()
// updates open_ and intensity_ via the controller
// controller says intensity is 0 if LED is turned off =>
//   we don't update intensity_ if controller reports 0, only set open_ to false
{
   ostringstream command; command.str("");
   long tmp = 0;
   command << addressChar_ << "LED X?";
   RETURN_ON_MM_ERROR( hub_->QueryCommandVerify(command.str(), "X="));
   RETURN_ON_MM_ERROR( hub_->ParseAnswerAfterEquals(tmp) );
   if (tmp > 0)
   {
      open_ = true;
      intensity_ = tmp;
   }
   else
   {
      open_ = false;
   }
   return DEVICE_OK;
}



////////////////
// action handlers

int CLED::OnSaveCardSettings(MM::PropertyBase* pProp, MM::ActionType eAct)
{
   string tmpstr;
   ostringstream command; command.str("");
   if (eAct == MM::AfterSet) {
      command << addressChar_ << "SS ";
      pProp->Get(tmpstr);
      if (tmpstr.compare(g_SaveSettingsOrig) == 0)
         return DEVICE_OK;
      if (tmpstr.compare(g_SaveSettingsDone) == 0)
         return DEVICE_OK;
      if (tmpstr.compare(g_SaveSettingsX) == 0)
         command << 'X';
      else if (tmpstr.compare(g_SaveSettingsY) == 0)
         command << 'X';
      else if (tmpstr.compare(g_SaveSettingsZ) == 0)
         command << 'Z';
      RETURN_ON_MM_ERROR (hub_->QueryCommandVerify(command.str(), ":A", (long)200));  // note 200ms delay added
      pProp->Set(g_SaveSettingsDone);
   }
   return DEVICE_OK;
}

int CLED::OnRefreshProperties(MM::PropertyBase* pProp, MM::ActionType eAct)
{
   string tmpstr;
   if (eAct == MM::AfterSet) {
      pProp->Get(tmpstr);
      if (tmpstr.compare(g_YesState) == 0)
         refreshProps_ = true;
      else
         refreshProps_ = false;
   }
   return DEVICE_OK;
}

int CLED::OnIntensity(MM::PropertyBase* pProp, MM::ActionType eAct)
{
   ostringstream command; command.str("");
   long tmp = 0;
   if (eAct == MM::BeforeGet)
   {
      if (!refreshProps_ && initialized_)
         return DEVICE_OK;
      UpdateOpenIntensity();  // will set intensity_ unless LED is turned off
      if (!pProp->Set((long)intensity_))
         return DEVICE_INVALID_PROPERTY_VALUE;
   }
   else if (eAct == MM::AfterSet) {
      pProp->Get(tmp);
      if(open_)  // if we are closed then don't actually want to set the controller, only the internal
      {
         command << addressChar_ << "LED X=" << tmp;
         RETURN_ON_MM_ERROR ( hub_->QueryCommandVerify(command.str(), ":A") );
      }
      intensity_ = tmp;
   }
   return DEVICE_OK;
}

int CLED::OnState(MM::PropertyBase* pProp, MM::ActionType eAct)
{
   if (eAct == MM::BeforeGet)
   {
      if(open_)
      {
         pProp->Set(g_OpenState);
      }
      else
      {
         pProp->Set(g_ClosedState);
      }
   }
   else if (eAct == MM::AfterSet)
   {
      string tmpstr;
      pProp->Get(tmpstr);
      RETURN_ON_MM_ERROR ( SetOpen(tmpstr.compare(g_OpenState) == 0) );
   }

   return DEVICE_OK;
}
