/*
 * jMemorize - Learning made easy (and fun) - A Leitner flashcards tool
 * Copyright(C) 2004-2006 Riad Djemili
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 1, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */
package jmemorize.gui.swing.panels;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.Timer;

import jmemorize.core.LC;
import jmemorize.core.Localization;
import jmemorize.core.learn.LearnSession;
import jmemorize.core.learn.LearnSessionObserver;
import jmemorize.core.learn.LearnSettings;
import jmemorize.gui.swing.Main;

/**
 * @author djemili
 */
public class TimerPanel extends JPanel implements ActionListener, LearnSessionObserver
{
    // show two digits fully
    private DecimalFormat m_formater  = new DecimalFormat("##00"); //$NON-NLS-1$

    private int           m_secondsPassed;
    private int           m_secondsTarget;

    private Timer         m_timer;

    private JProgressBar  m_bar       = new JProgressBar();
    private JTextField    m_textField = new JTextField();

    private LearnSession  m_learnSession;
    
    public TimerPanel()
    {
        Main.getInstance().addLearnSessionObserver(this);
    }
    
    /* (non-Javadoc)
     * @see jmemorize.core.learn.LearnSessionObserver#sessionStarted
     */
    public void sessionStarted(LearnSession session)
    {
        LearnSettings settings = session.getSettings();
        if (settings.isTimeLimitEnabled())
        {
            start(session, settings.getTimeLimit() * 60);
        }
        else
        {
            start(session, -1);
        }
    }
    
    /* (non-Javadoc)
     * @see jmemorize.core.learn.LearnSessionObserver#sessionEnded
     */
    public void sessionEnded(LearnSession session)
    {
        m_timer.stop();
    }

    /**
     * Is called every second and updates the timer representation.
     */
    public void actionPerformed(ActionEvent evt)
    {
        m_secondsPassed++;
    
        if (m_secondsTarget > -1)
        {
            m_bar.setValue((100 * m_secondsPassed) / m_secondsTarget);
            m_bar.setString(getTimeString());
        }
        else
        {
            m_textField.setText(getTimeString());
        }
        
        if (m_secondsPassed == m_secondsTarget)
        {
            m_timer.stop();
            m_learnSession.onTimer();
        }
    }

    private void start(LearnSession learnSession, int seconds)
    {
        m_learnSession = learnSession;
        m_secondsTarget = seconds;
        m_secondsPassed = 0;

        removeAll();
        setLayout(new BorderLayout());
        
        // if there is a time limit we show a progess bar
        // otherwise only a label
        if (m_secondsTarget > -1)
        {
            m_bar.setValue(0);
            m_bar.setStringPainted(true);
            m_bar.setString(getTimeString());
            add(m_bar, BorderLayout.CENTER);
        }
        else
        {
            m_textField.setText(getTimeString());
            m_textField.setHorizontalAlignment(JTextField.CENTER);
            m_textField.setEditable(false);
            add(m_textField, BorderLayout.CENTER);
        }
        
        // call action performer every second
        m_timer = new Timer(1000, this);
        m_timer.start();
    }
    
    /**
     * @return The string that is used to show time in progressbar.
     */
    protected String getTimeString()
    {
        // if progress bar
        if (m_secondsTarget > -1)
        {
            // if target time reached
            if (m_secondsTarget <= m_secondsPassed)
            {
                return timeString(m_secondsTarget, m_secondsTarget) + timeExtString(m_secondsTarget)
                     + " " + Localization.get("Time.PASSED") + "!"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            }
            else
            {
                return timeString(m_secondsPassed, m_secondsTarget) + " / " + //$NON-NLS-1$
                    timeString(m_secondsTarget, m_secondsTarget) + timeExtString(m_secondsTarget);
            }
        }
        // else show just a text label
        else
        {
            return timeString(m_secondsPassed, m_secondsPassed) +
                timeExtString(m_secondsPassed);
        }
    }
    
    protected String timeString(int seconds, int secondsTarget)
    {
        if (secondsTarget > 60*60) // show hours if over 60 minutes
        {
            return (seconds/(60*60)) + 
                ':' + m_formater.format((seconds/60)%60) +
                ':' + m_formater.format(seconds%60);
        }
        
        if (secondsTarget > 60) // show minutes if over 60 seconds
        {
            return (seconds/60) +":"+ m_formater.format(seconds%60); //$NON-NLS-1$
        }

        return Integer.toString(seconds);
    }
    
    protected String timeExtString(int seconds)
    {
        if (seconds > 60*60)
        {
            return " "+ Localization.get(LC.TIME_HOURS);    //$NON-NLS-1$
        }
        
        if (seconds > 60)
        {
            return " " + Localization.get(LC.TIME_MINUTES); //$NON-NLS-1$
        }
            
        return " " + Localization.get(LC.TIME_SECONDS);     //$NON-NLS-1$
    }
}
